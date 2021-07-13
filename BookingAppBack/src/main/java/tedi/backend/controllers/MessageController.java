package tedi.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tedi.backend.model.Department;
import tedi.backend.model.Message;
import tedi.backend.model.User;
import tedi.backend.repositories.DepartmentRepository;
import tedi.backend.repositories.MessageRepository;
import tedi.backend.repositories.UserRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
public class MessageController {

    MessageRepository messageRepository;
    UserRepository userRepository;
    DepartmentRepository departmentRepository;

    MessageController(UserRepository userRepository, MessageRepository messageRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.departmentRepository = departmentRepository;
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST') or hasRole('TENANT')")
    @PostMapping("/users/{userId}/messages")
    public ResponseEntity<?> message(@RequestBody Message message, @PathVariable Long userId, @RequestParam Long depId , @RequestParam(required = false) Long messId) {

        if( message.getText() != null || !message.getText().equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Department department = (Department)  Util.checkOptional(departmentRepository.findById(depId));
            User sender = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
            User reciever = (User) Util.checkOptional(userRepository.findById(userId));


            message.setFromUser(sender);
            message.setForUser(reciever);
            message.setAboutDepartment(department);

            if(messId != null){
               Message question = (Message) Util.checkOptional(messageRepository.findById(messId));
               message.setQuestion(question);
               message.setIsQuestion(false);
               question.setReply(message);

            }
            else
                message.setIsQuestion(true);



            messageRepository.save(message);

            return ResponseEntity.ok("\"Successfully sent!\"");
        }

        return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                + "\"status\": 400, "
                + "\"error\": \"Bad Request\", "
                + "\"message\": \"Empty message!\", "
                + "\"path\": \"/departments/" + userId.toString() +"/messages\"}"
        );
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/users/{userId}/messages/received")
    public List<Message> getReceivedMessages(@PathVariable Long userId, @RequestParam(required = false) Long depId){

            return messageRepository.getReceivedMessages(userId,depId);


    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/users/{userId}/messages/sent")
    public List<Message> getSentMessages(@PathVariable Long userId, @RequestParam(required = false) Long depId){

        return messageRepository.getSentMessages(userId,depId);

    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT') or hasRole('HOST')")
    @DeleteMapping("/users/{userId}/messages/{messId}")
    public ResponseEntity deleteMessage(@PathVariable Long userId, @PathVariable Long messId) {
        Message message =(Message) Util.checkOptional(this.messageRepository.findById(messId));
        Message reply = message.getReply();
        User sender = message.getFromUser();
        User reciever = message.getForUser();
        Department aboutDepartment = message.getAboutDepartment();


        if (reciever != null) {
            Set<Message> messageSet = new HashSet<Message>();
            for (Message mess : reciever.getMessagesFromUsers()) {
                if (message.getId() != mess.getId())
                    messageSet.add(mess);
            }
            reciever.setMessagesFromUsers(messageSet);
        }

        if (sender != null) {
            Set<Message> messageSet = new HashSet<Message>();
            for (Message mess : sender.getMessagesForUsers()) {
                if (message.getId() != mess.getId())
                    messageSet.add(mess);
            }
            sender.setMessagesForUsers(messageSet);
        }

        if (aboutDepartment != null) {
            Set<Message> messageSet = new HashSet<Message>();
            for (Message mess : aboutDepartment.getMessages()) {
                if (message.getId() != mess.getId())
                    messageSet.add(mess);
            }
            aboutDepartment.setMessages(messageSet);
        }



        if(reply !=  null) {
            sender = reply.getFromUser();
            reciever = reply.getForUser();
            aboutDepartment = reply.getAboutDepartment();


            if (reciever != null) {
                Set<Message> messageSet = new HashSet<Message>();
                for (Message mess : reciever.getMessagesFromUsers()) {
                    if (reply.getId() != mess.getId())
                        messageSet.add(mess);
                }
                reciever.setMessagesFromUsers(messageSet);
            }

            if (sender != null) {
                Set<Message> messageSet = new HashSet<Message>();
                for (Message mess : sender.getMessagesForUsers()) {
                    if (reply.getId() != mess.getId())
                        messageSet.add(mess);
                }
                sender.setMessagesForUsers(messageSet);
            }

            if (aboutDepartment != null) {
                Set<Message> messageSet = new HashSet<Message>();
                for (Message mess : aboutDepartment.getMessages()) {
                    if (reply.getId() != mess.getId())
                        messageSet.add(mess);
                }
                aboutDepartment.setMessages(messageSet);
            }

            reply.setQuestion(null);

        }


        this.messageRepository.delete(message);

        return ResponseEntity.ok("\"Successfully deleted\"");

    }



}

