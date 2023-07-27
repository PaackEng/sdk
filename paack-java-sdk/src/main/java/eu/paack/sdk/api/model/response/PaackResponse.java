package eu.paack.sdk.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaackResponse<T, E> {

    List<E> error;
    T data;
    int statusCode;
}
