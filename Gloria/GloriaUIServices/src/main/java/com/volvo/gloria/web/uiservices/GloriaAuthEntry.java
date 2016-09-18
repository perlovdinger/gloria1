/*
 * Copyright 2009 Volvo Information Technology AB
 *
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.volvo.gloria.web.uiservices;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Spring security entry point.
 */
public class GloriaAuthEntry extends LoginUrlAuthenticationEntryPoint {

    public GloriaAuthEntry() {
        super("#login");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        boolean isRestServiceCall = request.getRequestURI().contains("/rs");
        if (isRestServiceCall) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Not authorized to access");
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Not authorized to access");
        }
    }
}
