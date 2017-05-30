package org.test;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

@Theme("mytheme")
public class MyUI extends UI {
	Composite beanValidatorLayout;
	Composite jSRValidatorLayout;
    
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		
		
		setLocale(Locale.ENGLISH);
		
		beanValidatorLayout = new BeanValidatorLayout();
		jSRValidatorLayout = new JSRValidatorLayout();
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponents(beanValidatorLayout,jSRValidatorLayout);
		setContent(layout);
	}
	
	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}

}
