package com.zacharyohearn.sbme.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharyohearn.sbme.user.User;
import com.zacharyohearn.sbme.user.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    public MessageRepository messageRepository;
    public UserServiceClient userServiceClient;

    public MessageService(MessageRepository messageRepository, UserServiceClient userServiceClient) {
        this.messageRepository = messageRepository;
        this.userServiceClient = userServiceClient;
    }

    private List<Message> foundMessages = new ArrayList<>();

    public List<MessageDTO> getMessagesForUser(String firstName, String lastName, String dateOfBirth) {
        User user = userServiceClient.getUser(firstName, lastName, dateOfBirth);
        List<Message> list= messageRepository.findAllByUserId(user.getUserId());


        list.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getCreatedTimestamp().compareTo(o2.getCreatedTimestamp());
            }
        });

        return c(list, firstName, lastName, user.getUserId());
    }

    private List<MessageDTO> c(List<Message> list, String first, String last, int userId) {
        List<MessageDTO> retVal = new ArrayList<>();
        for (Message message: list) {
            retVal.add(MessageDTO.builder()
                    .messageId(message.getMessageId())
                    .messageBody(message.getMessageBody())
                    .createdTimeStamp(message.getCreatedTimestamp())
                    .firstName(first)
                    .lastName(last)
                    .userId(userId)
                    .build());
        }
        return retVal;
    }

    public MessageDTO getFirst(String firstName, String lastName, String dateOfBirth)
    {
        User user = userServiceClient.getUser(firstName, lastName, dateOfBirth);
        List<Message> list = messageRepository.findAllByUserId(user.getUserId());
        list.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getCreatedTimestamp().compareTo(o2.getCreatedTimestamp());
            }
        });
        return c(list.get(0), firstName, lastName, user.getUserId());
    }

    private MessageDTO c(Message message, String first, String last, int user) {
        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .messageBody(message.getMessageBody())
                .createdTimeStamp(message.getCreatedTimestamp())
                .firstName(first)
                .lastName(last)
                .userId(user)
                .build();
    }

    public List<MessageDTO> messageSearch(String firstName, String lastName, String dateOfBirth, String searchText) throws Exception {
        User user = null;
        try {
            user = userServiceClient.getUser(firstName, lastName, dateOfBirth);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        List<Message> AllUserMessages = messageRepository.findAllByUserId(user.getUserId());
        AllUserMessages.forEach(message -> {
            if (message.getMessageBody().contains(searchText)) {
                foundMessages.add(message);
            }
        });


        List<MessageDTO> messageDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(foundMessages) && foundMessages.size() > 0) {
            messageDTOS =  c(foundMessages, firstName, lastName, user.getUserId());
        }

        return messageDTOS;
    }


    public void createNewMessage(MessageDTO requestBody) throws JsonProcessingException
    {
        User user = null;
        try {
            user = userServiceClient.getUser(requestBody.getFirstName(), requestBody.getLastName(), requestBody.getDateOfBirth());
        } catch (Exception e) {
            ObjectMapper om = new ObjectMapper();
            log.error(om.writeValueAsString(e));
            e.printStackTrace();
        }
        Message newMessage = Message.builder()
                .userId(user.getUserId())
                .messageBody(requestBody.getMessageBody())
                .createdTimestamp(OffsetDateTime.now())
                .lastUpdatedTimestamp(OffsetDateTime.now())
                .build();
        messageRepository.save(newMessage);
    }

    public void updateMessage(String messageBody, int messageId, int userId)
    {
        Message messageDetails = messageRepository.findMessageByMessageIdAndUserId(messageId, userId);
        if (!ObjectUtils.isEmpty(messageDetails) && !ObjectUtils.isEmpty(messageDetails.getMessageBody())) {
            messageDetails.setMessageBody(messageBody);
            messageDetails.setLastUpdatedTimestamp(OffsetDateTime.now());
        }
        messageRepository.save(messageDetails);
    }

}
