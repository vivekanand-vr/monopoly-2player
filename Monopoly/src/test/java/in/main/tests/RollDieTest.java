package in.main.tests;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.main.restcontroller.GameController;
import in.main.service.IGameService;

@WebMvcTest(GameController.class)
public class RollDieTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IGameService gameService;

    @Test
    public void testPlayerATurn() throws Exception {
        String mockMessage = "Player A's turn";
        when(gameService.playerTransaction("A")).thenReturn(mockMessage);

        mockMvc.perform(MockMvcRequestBuilders.post("/roll-die/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mockMessage));
    }
    
    @Test
    public void testPlayerBTurn() throws Exception {
        String mockMessage = "Player B's turn";
        when(gameService.playerTransaction("B")).thenReturn(mockMessage);

        mockMvc.perform(MockMvcRequestBuilders.post("/roll-die/p2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mockMessage));
    }
}
