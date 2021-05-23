package com.dooars.mountain.config;

import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.constants.AllGolbalConstants;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	private final String SECRET = "mySecretKey";

	private final Set<String> byPassSet = new HashSet<>(Arrays.asList(AllEndPoints.AUTHENTICATION, AllEndPoints.ADD_CUSTOMER));
	private final Set<String> adminSet = new HashSet<>(Arrays.asList(AllEndPoints.ADD_ITEM, AllEndPoints.CHANGE_AVAILABILITY,
			AllEndPoints.ADD_ITEMS,	AllEndPoints.UPDATE_OFFER_ITEM, AllEndPoints.UPDATE_ITEM, AllEndPoints.DELETE_ITEM,
			AllEndPoints.MENU_GROUP_SERVICE, AllEndPoints.ADD_MENU_GROUP, AllEndPoints.DELETE_MENU_GROUP,
			AllEndPoints.UPDATE_MENU_GROUP, AllEndPoints.OFFER_SERVICE, AllEndPoints.ADD_OFFER, AllEndPoints.UPDATE_OFFER,
			AllEndPoints.DELETE_OFFER, AllEndPoints.FILE_SERVICE, AllEndPoints.UPLOAD_FILE));

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			if (!byPassSet.contains(request.getRequestURI()) && checkJWTToken(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null && claims.get("sub") != null) {
					ArrayList<String> roles = (ArrayList<String>) claims.get("authorities");
					if (AllGolbalConstants.ROLE_USER.equals(roles.get(0)) && adminSet.contains(request.getRequestURI()))
						SecurityContextHolder.clearContext();
					else
						setUpSpringAuthentication(claims);
				} else {
					SecurityContextHolder.clearContext();
				}
			}else {
				SecurityContextHolder.clearContext();
			}
			chain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}	

	private Claims validateToken(HttpServletRequest request) {
		String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
		return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
	}


	private void setUpSpringAuthentication(Claims claims) {
		@SuppressWarnings("unchecked")
		List<String> authorities =  (List<String>) claims.get("authorities");

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return false;
		return true;
	}

}
