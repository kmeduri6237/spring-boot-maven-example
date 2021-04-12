package com.zacharyohearn.sbme.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "messageId")
    private Integer messageId;

    @Column(name = "userId")
    private int userId;

    @Column(name = "messageBody")
    private String messageBody;

    @Column(name = "createdTimestamp")
    private OffsetDateTime createdTimestamp;

    @Column(name = "lastUpdatedTimestamp")
    private OffsetDateTime lastUpdatedTimestamp;
}
