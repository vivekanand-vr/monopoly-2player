package in.main.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.main.dto.PlayerDTO;
import in.main.restcontroller.GameController;
import in.main.service.IGameService;

@WebMvcTest(GameController.class)
public class GetCashDetailsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IGameService gameService;

    @Test
    public void testGetCashDetails() throws Exception {
        int mockCashA = 1000;
        int mockCashB = 1000;
        
        PlayerDTO mockPlayerDTO = new PlayerDTO();
        mockPlayerDTO.setCashA(mockCashA);
        mockPlayerDTO.setCashB(mockCashB);
        
        when(gameService.getCash(1)).thenReturn(mockCashA);
        when(gameService.getCash(2)).thenReturn(mockCashB);

        mockMvc.perform(MockMvcRequestBuilders.get("/get-cash-details")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cashA").value(mockCashA))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cashB").value(mockCashB));
    }
}
