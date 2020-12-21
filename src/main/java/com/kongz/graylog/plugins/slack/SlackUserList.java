package com.kongz.graylog.plugins.slack;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackUserList {

   @JsonProperty("ok")
   public Boolean ok;
   
   @JsonProperty("members")
   public List<SlackMember> members = null;

}