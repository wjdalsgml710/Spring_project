package com.example.sproject.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.sproject.configuration.WebMvcConfig;
import com.example.sproject.model.drive.DriveFileInfo;
import com.example.sproject.model.globals.GlobalsOfCg_num;
import com.example.sproject.model.globals.GlobalsOfMail;
import com.example.sproject.model.globals.GlobalsOfMl_type;
import com.example.sproject.model.login.Member;
import com.example.sproject.model.mail.Mail;
import com.example.sproject.model.mail.MailTo;
import com.example.sproject.service.common.CommonPaging;
import com.example.sproject.service.drive.DriveService;
import com.example.sproject.service.login.LoginService;
import com.example.sproject.service.mail.MailService;
import com.example.sproject.service.sample.EmailReader;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.mail.smtp.SMTPTransport;

@Controller
@RequestMapping("mail")
public class MailController {
	
	static final String MAIL_DOMAIN = "@teamworksgroup.shop";
	
	static final String MODE_RECEIVED = "received"; // ?????? ?????????
	static final String MODE_SENT = "sent"; // ?????? ?????????
	
	@Autowired
	EmailReader emailReader;
	@Autowired
	MailService mailService;
	@Autowired
	DriveService driveService;
	@Autowired
	LoginService loginService;
	
    /**
     * ?????? ?????? ????????? ????????????
     * @throws MessagingException 
     * @throws ParseException 
     */
    @RequestMapping(value ="", method= {RequestMethod.GET, RequestMethod.POST})
    public String index(String mode, Integer currentPage, @AuthenticationPrincipal Member principal, Model model) throws MessagingException, ParseException {
    	// ?????? ???????????? ?????? ?????? ??????
    	Mail mail = new Mail();
    	mail.setM_id(principal.getM_id());

    	
    	// ????????? ?????? ??????
    	if (mode == null || mode.equals("")) {
    		mode = MODE_RECEIVED;
    	}
    	if (mode.equals(MODE_RECEIVED)) {
    		// JSP ?????? ??????
    		model.addAttribute("mode", MODE_RECEIVED);
    		
        	// ?????? DB ??????????????????
        	mailService.updateMailDB();
        	Timestamp updateDateOfDb = mailService.findUpdateDateOfDb();
        	model.addAttribute("updateDateOfDb",updateDateOfDb);
        	
        	// ?????? ???????????? ?????? ?????? ??????
        	mail.setMl_type(1); // ?????? ??????
        	mail.setMl_is_deleted(0);		
    	} else if (mode.equals(MODE_SENT)) {
    		// JSP ?????? ??????
    		model.addAttribute("mode", MODE_SENT);
    		
        	// ?????? ???????????? ?????? ?????? ??????
        	mail.setMl_type(2); // ?????? ??????
        	mail.setMl_is_deleted(0);    		
    	}
    	
    	
    	// ????????? ??????
    	int rowPage = 10;
    	int pageBlock = 5;    	
    	CommonPaging commonPaging = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		commonPaging = new CommonPaging(mailService.countTotalMailReceived(mail), currentPage, rowPage, pageBlock);
    	} else if (mode.equals(MODE_SENT)) {
    		commonPaging = new CommonPaging(mailService.countTotalMailSent(mail), currentPage, rowPage, pageBlock);
    		//rowPage : ???????????? ?????????, pageBlock : ??????????????? ?????? ?????????????????????
    	}
    	System.out.println("commonPaging: " + commonPaging);
    	model.addAttribute("commonPaging", commonPaging);
    	mail.setRn_start(commonPaging.getStart());
    	mail.setRn_end(commonPaging.getEnd());
    	
    	// ????????? ????????? ?????? ?????? ????????????
    	List<Mail> listOfMail = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		listOfMail = mailService.listMailReceived(mail); 
    	} else if (mode.equals(MODE_SENT)) {
    		listOfMail = mailService.listMailSent(mail);
    	}
    	mailService.replaceStringForHtml(listOfMail);
    	model.addAttribute("listOfMail", listOfMail);
    	
    	// ?????? ??? ???????????????
    	mail.setRn_start(1);
    	mail.setRn_end(Integer.MAX_VALUE);
    	List<Mail> listOfMailForCounting = null;
    	if (mode.equals(MODE_RECEIVED)) {
    		listOfMailForCounting = mailService.listMailReceived(mail);
    	} else if (mode.equals(MODE_SENT)) {
    		listOfMailForCounting = mailService.listMailSent(mail);
    	}
    	int numRead = 0;
    	for (Mail m : listOfMailForCounting) {
    		if (m.getMl_is_read() == 1) {
    			++numRead;
    		}
    	}
    	model.addAttribute("numRead", numRead);
    	model.addAttribute("numUnread", listOfMailForCounting.size() - numRead);   
    	
    	
    	
        return "mail/mailMain";
    }
    
    /**
     * ?????? ?????? ??????
     * @param ml_num
     * @param principal
     * @param model
     * @return
     */
    @RequestMapping(value ="view/{ml_num:.+}", method= {RequestMethod.GET, RequestMethod.POST})
    public String view(@PathVariable int ml_num, @AuthenticationPrincipal Member principal, Model model) {
    	// ?????? ?????? ????????????
    	Mail mail = mailService.selectMail(ml_num);
    	mailService.replaceStringForHtml(mail);
    	model.addAttribute("mail", mail);
    	
    	// ?????? ?????? ?????? ????????????
    	List<MailTo> listOfMailTo = mailService.listMailTo(ml_num);
    	mailService.replaceStringForHtml(listOfMailTo);
    	
    	model.addAttribute("listOfMailTo", listOfMailTo);
    	
    	// ???????????? ?????? ????????????
    	List<DriveFileInfo> listOfDriveFileInfo = mailService.listDriveFileInfo(ml_num);
    	model.addAttribute("listOfDriveFileInfo", listOfDriveFileInfo);
    	
    	// ?????? ?????? ????????? ??????
    	if (!mailService.isAuthorized(mail, principal.getM_id())) {
    		return "login/denied";
    	}
    	
    	// ?????? ?????? ?????? ????????????
    	mailService.updateMailRead(ml_num);
    	
    	return "mail/mailView";
    }
    
    /**
     * ?????? ?????? ????????? ??????
     * @return
     */
    @RequestMapping(value ="writeForm", method= {RequestMethod.GET, RequestMethod.POST})
    public String writeForm(@RequestParam(value="ml_num", required = false) Integer ml_num, @RequestParam(value="chkArr[]", required = false) List<String> listOfM_id, @AuthenticationPrincipal Member principal, Model model) {
    	System.out.println("-- writeForm in MailController");
    	String email = "";
    	// ????????????
    	if (ml_num != null && ml_num > 0) {
    		Mail mail = mailService.selectMail(ml_num);
    		email = mailService.extractEmailAddress(mail.getMl_email());
    		model.addAttribute("email", email);
    	}
    	
    	// ?????? ?????? ????????? ????????? ????????? ???
    	else if (listOfM_id != null && listOfM_id.size() > 0) {
    		for (String m_id : listOfM_id) {
    			Member member = (Member) loginService.loadUserByUsername(m_id);
    			email += member.getM_email() + " ";
    		}
    		model.addAttribute("email", email);
    	}
    	
    	model.addAttribute("principal", principal);
    	return "mail/mailWriteForm";
    }
    
    
    
	/**
	 * ?????? ?????????
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	@RequestMapping(value ="send", method= {RequestMethod.POST})
	public String mailSend(Mail mail, String addressTo, @RequestParam("multipartFile") List<MultipartFile> listOfMultipartFile, @AuthenticationPrincipal Member principal, Model model) throws IOException, Exception {		
		// ?????? ????????? ????????? db??? ?????? ????????????
		List<DriveFileInfo> listOfDriveFileInfo = new ArrayList<DriveFileInfo>();
		for (MultipartFile multipartFile : listOfMultipartFile) {
			if (multipartFile.getSize() > 0) {
				// ?????? ????????? ?????????
				String uploadPath = WebMvcConfig.RESOURCE_PATH + "/drive";
			    String dv_id = driveService.uploadFile(multipartFile.getOriginalFilename(), multipartFile.getBytes(), uploadPath);
			    
			    // DRIVE ???????????? ?????? ?????? ?????? 
			    DriveFileInfo driveFile = new DriveFileInfo(dv_id, principal.getM_id(), multipartFile.getOriginalFilename(), null, GlobalsOfCg_num.DRIVE_SIGN);
			    driveService.insertDriveFileInfo(driveFile);
			    
				// ???????????? ??????
			    listOfDriveFileInfo.add(driveService.selectOneDriveFileInfo(dv_id));
			}			
		}
		
		// ?????? ?????????
		mailService.sendMail(principal, mail, addressTo, listOfDriveFileInfo);
		
		// ?????? ?????? db??? ????????????
		mailService.insertMailSent(mail);
		mailService.insertMailTo(mail.getMl_num(), addressTo);
		if (listOfDriveFileInfo.size() > 0) {
			mailService.insertMailFile(mail.getMl_num(),listOfDriveFileInfo);
		}
		
		return "redirect:/mail";
	}
	
	/**
	 * ?????? ????????????
	 * @param ml_num
	 * @param listOfMl_num
	 * @param principal
	 * @return
	 */
    @RequestMapping(value="delete", method= {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(required = false) Integer ml_num, @RequestParam(value="chkArr[]", required = false) List<Integer> listOfMl_num, @AuthenticationPrincipal Member principal) {   	
    	// ?????? ?????? ?????? ????????? ?????? ??????
    	if (ml_num != null && ml_num > 0) {
        	// ?????? ????????????
        	Mail mail = mailService.selectMail(ml_num);
        	
        	// ?????? ?????? ????????? ??????
        	if (!mailService.isAuthorized(mail, principal.getM_id())) {
        		return "login/denied";
        	}
        	
        	// ?????? ??????
        	mailService.deleteMail(ml_num);
        	return "redirect:/mail";
    	}
    	

    	// ?????? ?????? ?????? ????????? ?????? ?????? (ajax)
    	if (listOfMl_num != null && listOfMl_num.size() > 0) {
    		for (int ml_numInList : listOfMl_num) {
            	// ?????? ????????????
            	Mail mail = mailService.selectMail(ml_numInList);
            	
            	// ?????? ?????? ????????? ??????
            	if (!mailService.isAuthorized(mail, principal.getM_id())) {
            		return "forward:/mail/api/result?result=fail";
            	}
            	
            	// ?????? ??????
            	mailService.deleteMail(ml_numInList);    			
    		}
    		return "forward:/mail/api/result?result=success";
    	}
    	
    	return null;
    }
    



    // ????????? ???????????? ?????????
    @RequestMapping(value="test", method= {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String test(String text) {
    	return mailService.extractEmailAddress(text);
    }
    
    @RequestMapping(value="api/result", method= {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String apiResult(String result) {
    	return result;
    }
}
