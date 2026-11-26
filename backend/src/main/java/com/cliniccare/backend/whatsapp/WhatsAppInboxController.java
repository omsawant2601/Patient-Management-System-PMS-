package com.cliniccare.backend.whatsapp;

import com.cliniccare.backend.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/whatsapp/inbox")
public class WhatsAppInboxController {
    private final WhatsAppInboundMessageRepository messages;

    public WhatsAppInboxController(WhatsAppInboundMessageRepository messages) {
        this.messages=messages;
    }

    @GetMapping
    public ApiResponse<Page<WhatsAppInboxDtos.Message>> list(
            @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="50") int size) {
        PageRequest request=PageRequest.of(Math.max(0,page),Math.min(100,Math.max(1,size)),
                Sort.by(Sort.Direction.DESC,"receivedAt"));
        Page<WhatsAppInboxDtos.Message> result=messages.findAllByOrderByReceivedAtDesc(request)
                .map(message->new WhatsAppInboxDtos.Message(message.getId(),message.getSenderPhone(),
                        message.getSenderName(),message.getMessageType(),message.getContent(),message.getReceivedAt()));
        return ApiResponse.success("WhatsApp inbox",result);
    }
}
