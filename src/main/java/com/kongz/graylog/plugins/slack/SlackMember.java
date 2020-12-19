package com.kongz.graylog.plugins.slack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackMember {

   @JsonProperty("id")
   public String id;

   @JsonProperty("team_id")
   public String teamId;
   
   @JsonProperty("name")
   public String name;
   
   @JsonProperty("deleted")
   public Boolean deleted;
   
   @JsonProperty("profile")
   public SlackProfile profile;
   
   @JsonProperty("is_bot")
   public Boolean isBot;
   
   @JsonProperty("is_app_user")
   public Boolean isAppUser;
   
   @JsonProperty("updated")
   public Integer updated;

}
