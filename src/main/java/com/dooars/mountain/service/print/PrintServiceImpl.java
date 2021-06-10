package com.dooars.mountain.service.print;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.order.Order;
import com.dooars.mountain.model.order.OrderItem;
import com.dooars.mountain.repository.customer.CustomerRepository;
import com.dooars.mountain.service.s3.AWSS3Service;
import com.dooars.mountain.service.s3.AWSS3ServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Prantik Guha on 08-06-2021
 **/
@Service
public class PrintServiceImpl implements PrintService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final AWSS3Service awss3Service;

    @Autowired
    PrintServiceImpl(CustomerRepository customerRepository, AWSS3Service awss3Service) {
        this.customerRepository = customerRepository;
        this.awss3Service = awss3Service;
    }

    @Override
    public String createKOT(long orderId) throws BaseException {
        LOGGER.trace("Entering into createKOT method in PrintServiceImpl class with {}", orderId);
        Order order = customerRepository.getOrderById(orderId);
        Rectangle pageSize = new Rectangle(227, 842);
        Document document = new Document(pageSize,5,0,10,10);
        String fileName = "";
        try {

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(85);
            table.setWidths(new int[]{3, 1});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("\n\nName\n\n", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("\n\nQty\n\n", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            int totalQuantity = 0;
            for (OrderItem orderItem : order.getOrderDetails().getItems()) {

                PdfPCell cell;

                cell = new PdfPCell(new Phrase(orderItem.getItemName()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                totalQuantity += orderItem.getQuantity();
                cell = new PdfPCell(new Phrase(String.valueOf(orderItem.getQuantity())));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("\n\nTotal Items"));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n" + String.valueOf(totalQuantity)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);


            fileName = order.getOrderId() + "_kot.pdf";
            File file = new File(fileName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, fileOutputStream);


            document.open();
            setHeader(document);
            setDescription(document, order);
            document.add(table);
            document.close();

            awss3Service.uploadFile(file);
            file.delete();

        } catch (DocumentException | IOException ex) {
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
        return fileName;
    }

    @Override
    public String createBill(long orderId) throws BaseException {
        LOGGER.trace("Entering into createBill method in PrintServiceImpl class with {}", orderId);
        Order order = customerRepository.getOrderById(orderId);
        Rectangle pageSize = new Rectangle(227, 842);
        Document document = new Document(pageSize,5,0,10,10);
        String fileName = "";
        try {

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(85);
            table.setWidths(new int[]{3, 1, 2});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("\n\nName\n\n", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("\n\nQty\n\n", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("\n\nAmount\n\n", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            int totalQuantity = 0;
            float totalSum = 0;
            for (OrderItem orderItem : order.getOrderDetails().getItems()) {

                PdfPCell cell;

                cell = new PdfPCell(new Phrase(orderItem.getItemName()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                totalQuantity += orderItem.getQuantity();
                cell = new PdfPCell(new Phrase(String.valueOf(orderItem.getQuantity())));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                totalSum += orderItem.getTotal();
                cell = new PdfPCell(new Phrase(String.valueOf(orderItem.getTotal())));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("\n\n"));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n" + String.valueOf(totalQuantity)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("\n\n" + String.valueOf(totalSum)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);



            cell = new PdfPCell(new Phrase("Delivery Charges", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf("+")));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(order.getOrderDetails().getDeliveryCharges()), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Packaging Charge", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf("+")));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(order.getOrderDetails().getTotalPackingCharge()), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Offer", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(order.getOrderDetails().getOffer() + "%"), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            float offer = (float) (totalSum * order.getOrderDetails().getOffer() / 100);
            cell = new PdfPCell(new Phrase(String.valueOf(offer), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sub Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf("")));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(order.getOrderDetails().getTotal()), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);


            fileName = order.getOrderId() + "_bill.pdf";
            File file = new File(fileName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, fileOutputStream);


            document.open();
            setHeader1(document);
            setDescription(document, order);
            document.add(table);
            document.close();

            awss3Service.uploadFile(file);
            file.delete();

        } catch (DocumentException | IOException ex) {
            throw new BaseException(ex.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
        return fileName;
    }

    @Override
    public void deleteBill(String fileName) throws BaseException {
        LOGGER.trace("Entering into deleteBill method in PrintServiceImpl class with {}", fileName);
        awss3Service.deleteFile(fileName);
    }

    @Override
    public void deleteKot(String fileName) throws BaseException {
        LOGGER.trace("Entering into deleteKot method in PrintServiceImpl class with {}", fileName);
        awss3Service.deleteFile(fileName);
    }

    private void setHeader1(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15);
        paragraph.setFont(boldFont);
        paragraph.add("\n\t\t\tHotel Dooars Mountain\n");
        document.add(paragraph);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
        paragraph1.setFont(normalFont);
        paragraph1.add("(An Unit of Dooars Mountain Residence Co)");
        document.add(paragraph1);
        Paragraph paragraph2 = new Paragraph();
        paragraph2.setAlignment(Element.ALIGN_CENTER);
        paragraph2.setFont(normalFont);
        paragraph2.add("[An ISO 9001-2015 Certified Hotel]");
        document.add(paragraph2);
        Paragraph paragraph3 = new Paragraph();
        paragraph3.setAlignment(Element.ALIGN_CENTER);
        paragraph3.setFont(normalFont);
        paragraph3.add("BF road, Alipurduar-736121, West Bengal");
        document.add(paragraph3);
    }

    private void setHeader(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        paragraph.setFont(boldFont);
        paragraph.add("\n\t\t\tHotel Dooars Mountain\n");
        document.add(paragraph);
    }

    private void setDescription(Document document, Order order) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add("---------------------------------------------"+ "\n");
        paragraph.add("HOME DELIVERY"+ "\n");
        paragraph.add("---------------------------------------------"+ "\n");
        paragraph.setFont(boldFont);
        paragraph.add("Order ID  :  ");
        paragraph.setFont(normalFont);
        paragraph.add(String.valueOf(order.getOrderId()) + "\n");
        paragraph.setFont(boldFont);
        paragraph.add("Customer Name  :  ");
        paragraph.setFont(normalFont);
        paragraph.add( order.getCustName() + "\n");
        ZoneId zid = ZoneId.of("Asia/Kolkata");
        ZonedDateTime lt = ZonedDateTime.now(zid);
        paragraph.setFont(boldFont);
        paragraph.add("Time  :  ");
        paragraph.setFont(normalFont);
        paragraph.add(lt.getDayOfMonth() + "-" + lt.getMonth() + "-" + lt.getYear() + "  " + lt.getHour() + ":" + lt.getMinute() + ":" + lt.getSecond());
        paragraph.add("\n---------------------------------------------"+ "\n");
        paragraph.add("\n\n");
        document.add(paragraph);
    }
}
