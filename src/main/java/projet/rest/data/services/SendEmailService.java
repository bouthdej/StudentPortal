package projet.rest.data.services;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import projet.rest.data.models.UserEntity;


@Service
@Component
public class SendEmailService {

	private JavaMailSender javaMailSender;
	@Autowired
	public SendEmailService(JavaMailSender javaMailSender) {
		this.javaMailSender= javaMailSender;
	}
/*	@Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
	
	 @Autowired
	private JavaMailSender JavaMailSender = javaMailSender();
*/	
	public void sendEmail(String to,String body,String topic, List<UserEntity> receivers) {
		Properties authProps = new Properties();
		authProps.put("mail.smtp","587");
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom("youradvancer@gmail.com");
		mail.setCc(to);
		//mail.setTo("youradvancerrequest@gmail.com");
		for (UserEntity userEntity : receivers) {
			mail.setTo(userEntity.getEmail());
		}
		mail.setSubject(topic);
		mail.setText(body);
		javaMailSender.send(mail);
	}
	//helper.setTo(new String[]{"email1@test.com", "email2@test.com"});
	public void sendEmailToMany(String from,String body,String topic, List<UserEntity> students) {
		System.out.println("test 4");
		Properties authProps = new Properties();
		authProps.put("mail.smtp","587");
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom("youradvancer@gmail.com");
		mail.setCc(from);
		String[] classmails = new String[students.size()];
		for (int i = 0; i < classmails.length; i++) {
			System.out.println("$$$$"+classmails[i]);
		}
		int i = 0;
		for (UserEntity userEntity : students) {
			classmails[i]=userEntity.getEmail();
			i++;
		}
		mail.setTo(classmails);
		mail.setSubject(topic);
		mail.setText(body);
		javaMailSender.send(mail);
	}
	public void welcomeMail(String to,String username) {
		String body,topic;
		Properties authProps = new Properties();
		authProps.put("mail.smtp","587");
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom("youradvancer@gmail.com");
		mail.setTo(to);
		topic= " Welcome "+username+" to Your-Advancer Community";
		mail.setSubject(topic);
		body="Welcome to Your Advancer \r\n"
				+ "Access in-depth resources\r\n"
				+ "Take advantage of available resources to solve problems and advance your career."
				+ " Check out our developer"
				+ " how-tos and getting started guides, videos, cheat sheets, and more.";
		mail.setText(body);
		javaMailSender.send(mail);
	}
	public void verifyEmail(String to,String topic,String body) {
		Properties authProps = new Properties();
		authProps.put("mail.smtp","587");
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom("youradvancer@gmail.com");
		mail.setTo(to);
		mail.setSubject(topic);
		mail.setText(body);
		javaMailSender.send(mail);
	}
	public void changePassword(String to,String topic,String body) {
		Properties authProps = new Properties();
		authProps.put("mail.smtp","587");
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom("youradvancer@gmail.com");
		mail.setTo(to);
		mail.setSubject(topic);
		mail.setText(body);
		javaMailSender.send(mail);
	}
}