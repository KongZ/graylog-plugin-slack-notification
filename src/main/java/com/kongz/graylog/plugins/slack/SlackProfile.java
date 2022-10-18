package com.kongz.graylog.plugins.slack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackProfile {

   @JsonProperty("title")
   public String title;

   @JsonProperty("phone")
   public String phone;

   @JsonProperty("skype")
   public String skype;

   @JsonProperty("real_name")
   public String realName;

   @JsonProperty("real_name_normalized")
   public String realNameNormalized;

   @JsonProperty("display_name")
   public String displayName;

   @JsonProperty("display_name_normalized")
   public String displayNameNormalized;

   @JsonProperty("status_text")
   public String statusText;

   @JsonProperty("status_emoji")
   public String statusEmoji;

   @JsonProperty("status_expiration")
   public Integer statusExpiration;

   @JsonProperty("avatar_hash")
   public String avatarHash;

   @JsonProperty("image_original")
   public String imageOriginal;

   @JsonProperty("is_custom_image")
   public Boolean isCustomImage;

   @JsonProperty("first_name")
   public String firstName;

   @JsonProperty("last_name")
   public String lastName;

   @JsonProperty("email")
   public String email;

   @JsonProperty("image_24")
   public String image24;

   @JsonProperty("image_32")
   public String image32;

   @JsonProperty("image_48")
   public String image48;

   @JsonProperty("image_72")
   public String image72;

   @JsonProperty("image_192")
   public String image192;

   @JsonProperty("image_512")
   public String image512;

   @JsonProperty("image_1024")
   public String image1024;

   @JsonProperty("status_text_canonical")
   public String statusTextCanonical;

   @JsonProperty("team")
   public String team;
}
