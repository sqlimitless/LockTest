package everex.redisdemo.enitty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupDori2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    @Version
    private int version;

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            --this.quantity;
        }
    }
}
