package vn.acgroup.service.mqtt;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.acgroup.config.EmqxClientConfig;
import vn.acgroup.controllers.emqx.PublishRequest;
import vn.acgroup.controllers.emqx.PublishResponse;

@Service
public class MqttService {

  @Autowired
  @Qualifier("emqxClient")
  RestTemplate emqxClient;

  public void sendToMqtt(HashMap<String, String> val, String topic) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    PublishRequest publishRequest = new PublishRequest();
    publishRequest.setPayload(mapper.writeValueAsString(val));
    publishRequest.setTopic(topic);
    emqxClient.postForObject(
        EmqxClientConfig.EMQX_PUBLISH_URL, publishRequest, PublishResponse.class);
  }
}
