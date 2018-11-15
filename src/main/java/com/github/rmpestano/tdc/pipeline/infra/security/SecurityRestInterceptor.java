package com.github.rmpestano.tdc.pipeline.infra.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.github.adminfaces.template.exception.AccessDeniedException;

@Provider
@RestSecured
public class SecurityRestInterceptor implements ContainerRequestFilter, Serializable, DynamicFeature {
	
	  private static final Logger LOG = Logger.getLogger(SecurityRestInterceptor.class.getName());  
	
	  @Inject
	  LogonMB            logonMB;

	  @Context
	  HttpServletRequest request;

	  @Context
	  HttpHeaders        headers;
	  
	  @Override
	  public void filter(ContainerRequestContext crc) throws IOException {
	    try {
	      logon();
	    } catch (Exception e) {
	      LOG.log(Level.SEVERE, "Invalid credentials.", e);
	      throw new AccessDeniedException("Invalid credentials.");
	    }
	  }

	  private void logon() {
	      String username = getHeader("user");
	      if(username != null) {
	    	  logonMB.login(username);
	      } else {
	    	  throw new RuntimeException("Access denied");
	      }
	  }

	  

	  private String getHeader(String name) {
	    List<String> header = headers.getRequestHeader(name);
	    if (header == null || header.isEmpty()) {
	      return null;
	    }
	    return header.get(0);
	  }

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
	  	if(resourceInfo.getResourceMethod().getAnnotation(RestSecured.class) != null
				|| resourceInfo.getResourceMethod().getDeclaringClass().isAnnotationPresent(RestSecured.class)) {

			context.register(resourceInfo);
		}
	}
}
