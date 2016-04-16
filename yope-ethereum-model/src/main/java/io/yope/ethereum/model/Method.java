package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder(builderClassName="Builder", toBuilder=true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Method {
    public enum Type {MODIFY, RUN};
    private Type type;
    private Object[] args;
    private String name;
}
