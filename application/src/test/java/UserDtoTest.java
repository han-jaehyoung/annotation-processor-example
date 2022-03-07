import com.yanolja.sample.app.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class UserDtoTest {

    @Test
    void userDtoTest() {
        UserDto userDto = new UserDto(1L, 20, "yanolja", "seoul", "developer");
        assertSame( 1L, userDto.getId());
        assertSame( 20, userDto.getAge());
        assertSame( "yanolja", userDto.getName());
        assertSame( "seoul", userDto.getAddress());
        assertSame( "developer", userDto.getJob());
    }
}
