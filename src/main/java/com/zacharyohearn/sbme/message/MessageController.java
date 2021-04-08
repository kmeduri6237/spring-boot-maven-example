package com.zacharyohearn.sbme.message;

import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/messages")
    public ResponseEntity getMessagesForUser(@RequestParam String firstName, @RequestParam String lastName) {
        return ResponseEntity.ok(messageService.getMessagesForUser(firstName, lastName));
    }

    @GetMapping("/messages/first")
    public ResponseEntity getFirstMessage(@RequestParam String firstName, @RequestParam String lastName, @RequestParam int age) {
        return ResponseEntity.ok(messageService.getFirst(firstName, lastName));
    }

    @GetMapping("/messages/search")
    public ResponseEntity searchMessagesByFirstName(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String searchText) {
        return ResponseEntity.ok(messageService.messageSearch(firstName, lastName, searchText));
    }

    @SneakyThrows
    @PostMapping("/createnewmessage")
    public ResponseEntity createNewMessage(@RequestBody String requestBody) {
        String body = JsonPath.from(requestBody).getString("body");
        String firstName = JsonPath.from(requestBody).getString("firstName");
        String lastName = JsonPath.from(requestBody).getString("lastName");
        messageService.createNewMessage(body, firstName, lastName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/editmesssage/{messageId}")
    public ResponseEntity editmessage(@PathVariable Integer messageId, @RequestBody String mesagebody) {
        messageService.editmessage(messageId, mesagebody);
        return ResponseEntity.ok().build();
    }
}
