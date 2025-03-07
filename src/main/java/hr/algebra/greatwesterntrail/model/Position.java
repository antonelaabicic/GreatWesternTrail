package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Position implements Serializable {
    @Serial
    private static final long serialVersionUID = 1004L;

    private int row;
    private int column;
}
