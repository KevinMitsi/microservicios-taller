package com.taller.msvc_orchestrator.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEntity {
    @Id
    private String id;
    private String name;
    private String channel;
    private String subject;
    private String body;
    private boolean active = true;

}