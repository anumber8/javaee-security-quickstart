package controller;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import facade.UserFacade;
import model.User;
import util.Resources;

@Model
public class Register {

    @Inject FacesContext context;
    @Inject UserFacade userFacade;
    
    private User newUser;

    @PostConstruct
    public void initNewUser() {
        newUser = new User();
    }
    
    /**
     * Register a new user
     * 
     * @return Navigation to /views/account.xhtml
     */
    public String register() {
        try {
            // save the password before encoding
            String password = newUser.getPassword();
            User user = userFacade.encodeAndSave(newUser);
            if ( user == null ) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration failed!", "User Already Exists" ));
                return null;
            } else {
                // login user            
                ExternalContext externalContext = context.getExternalContext();
                HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
                request.login(newUser.getEmail(), password);
                externalContext.getSessionMap().put("user", user);                
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed!", Resources.getRootErrorMessage(e) ));
            return null;
        }
        // navigate
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful!", "" ));
        return "/views/account.xhtml";
    }

    /**
     * New User Field
     * @return newUser
     */
    public User getNewUser() {
        return newUser;
    }
    /**
     * New User Field
     * @param newUser
     */
    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }
}
