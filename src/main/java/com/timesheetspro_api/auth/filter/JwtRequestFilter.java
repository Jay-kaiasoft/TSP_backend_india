package com.timesheetspro_api.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.constants.Constants;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.model.users.Users;
import com.timesheetspro_api.companyEmployee.service.CompanyEmployeeService;
import com.timesheetspro_api.users.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(JwtRequestFilter.class);

    @Autowired
    UserService userService;

    @Autowired
    CompanyEmployeeService companyEmployeeService;

    @Autowired
    JwtTokenUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = req.getHeader(Constants.REQUEST_HEADER_AUTHORIZATION);
        String requestUri = req.getRequestURI();
        log.debug("Validating request with URI : " + requestUri);

        String username = "";
        String jwtToken = "";
        String companyId = "";

        if (null != authorizationHeader && authorizationHeader.startsWith(Constants.AUTHORIZATION_BEARER)) {
            jwtToken = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwtToken);
            companyId = jwtUtil.extractCompanyId(jwtToken);

            Long userId = jwtUtil.extractUserId(jwtToken);
            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(username + "#_#" + companyId);
                Map<String, Object> userData = new HashMap<>();

                Users user = this.userService.getUser(userId);
                if (user == null) {
                    CompanyEmployeeDto companyEmployee = this.companyEmployeeService.getEmployee(Integer.parseInt(userId.toString()));
                    userData.put("userName", companyEmployee.getUserName());
                } else {
                    userData.put("userName", user.getUsername());
                }
                req.setAttribute("userId", userId);

                if (jwtUtil.validateToken(jwtToken, userData)) {
                    UsernamePasswordAuthenticationToken springToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    springToken.setDetails(new WebAuthenticationDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(springToken);
                }
            }
            chain.doFilter(req, res);

        } else if (requestUri.contains("/getTimezones") || requestUri.contains("/user/uploadProfileImage") || requestUri.contains("/uploadFile") || requestUri.contains("/user/generateResetLink") || requestUri.contains("/user/validateToken") || requestUri.contains("/user/resetPassword") || requestUri.contains("/user/create") || requestUri.contains("/user/login") || requestUri.contains("/inout/clockInOut")) {
            chain.doFilter(req, res);
        } else {
            log.error("Invalid request URI : " + requestUri);

            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put(Constants.MSG, Constants.INVALID_TOKEN);
            returnMap.put(Constants.CODE, HttpStatus.FORBIDDEN.value());
            returnMap.put(Constants.STATUS, Constants.STATUS_FAILURE);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(returnMap);

            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF_8);
            res.setStatus(HttpStatus.FORBIDDEN.value());
            res.getWriter().write(json);
            return;
        }

    }
}
