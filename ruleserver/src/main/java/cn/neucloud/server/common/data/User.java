/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.neucloud.server.common.data;


import cn.neucloud.server.common.data.id.UserId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class User extends SearchTextBased<UserId> implements HasName {

    private static final long serialVersionUID = 8250339805336035966L;

    private UserId tenantId;
    //private UserId customerId;
    private String email;
    private String firstName;
    private String lastName;
    private JsonNode additionalInfo;

    public User() {
        super();
    }

    public User(UserId id) {
        super(id);
    }

    public User(User user) {
        super(user);
        this.tenantId = user.getUserId();
        //this.customerId = user.getCustomerId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.additionalInfo = user.getAdditionalInfo();
    }

    public UserId getUserId() {
        return tenantId;
    }

    public void setUserId(UserId tenantId) {
        this.tenantId = tenantId;
    }
    //
    //public UserId getCustomerId() {
    //    return customerId;
    //}
    //
    //public void setCustomerId(UserId customerId) {
    //    this.customerId = customerId;
    //}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return email;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public JsonNode getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(JsonNode additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String getSearchText() {
        return email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
        //result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (additionalInfo == null) {
            if (other.additionalInfo != null)
                return false;
        } else if (!additionalInfo.equals(other.additionalInfo))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [userId=");
        builder.append(tenantId);
        builder.append(", email=");
        builder.append(email);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", additionalInfo=");
        builder.append(additionalInfo);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
