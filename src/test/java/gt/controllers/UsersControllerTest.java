package gt.controllers;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UsersControllerTest {

    @Mock
    private UsersRepository mockUsersRepository;

    @Mock
    private User mockUser;

    private UsersController usersController;

    @Before
    public void before() {
        given(mockUsersRepository.findByAddress("test")).willReturn(mockUser);

        usersController = new UsersController(mockUsersRepository);
    }

    @Test
    public void shouldBlockUser() {
        ResponseEntity responseEntity = usersController.blockUser("test");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
        verify(mockUser).setBlocked(true);
        verify(mockUsersRepository).save(mockUser);
    }

    @Test
    public void shouldNotBlockNonexistentUser() {
        ResponseEntity responseEntity = usersController.blockUser("test-wrong");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void shouldUnblockUser() {
        ResponseEntity responseEntity = usersController.unblockUser("test");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
        verify(mockUser).setBlocked(false);
        verify(mockUsersRepository).save(mockUser);
    }

    @Test
    public void shouldNotUnblockNonexistentUser() {
        ResponseEntity responseEntity = usersController.unblockUser("test-wrong");

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

}