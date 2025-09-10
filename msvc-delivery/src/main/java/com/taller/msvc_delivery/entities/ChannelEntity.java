package com.taller.msvc_delivery.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelEntity {
    @Id
    private String id;
    private String key;
    private String displayName;
    private boolean enabled = true;
}