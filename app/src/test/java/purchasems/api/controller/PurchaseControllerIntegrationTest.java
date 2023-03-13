package purchasems.api.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import purchasems.PurchaseMsResponseUtil;
import purchasems.persistence.repository.OrderLineRepository;
import purchasems.persistence.repository.PurchaseRepository;
import purchasems.service.PurchaseService;

@WebMvcTest(PurchaseController.class)
public class PurchaseControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PurchaseService purchaseService;

  @MockBean private PurchaseRepository purchaseRepository;
  @MockBean private OrderLineRepository orderLineRepository;

  @Test
  public void testGetPurchase_whenValid_then200() throws Exception {
    given(purchaseService.getPurchase("PUR0000001"))
        .willReturn(PurchaseMsResponseUtil.defaultPurchaseResponse());
    mockMvc
        .perform(get("/PUR0000001").with(jwt()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "{\"id\":\"PUR0000001\",\"order_id\":\"ORD0000001\",\"supplier\":{\"id\":\"SUP0000001\",\"name\":\"Supplier A\"},"
                        + "\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\",\"name\":\"Heineken\",\"price\":10,\"category\":\"beer\"}"
                        + "}],\"status\":\"processing\",\"datetime\":\"07-29-2022T12:16:00.000Z\"}"));
  }

  @Test
  public void testGetPurchase_whenInvalidPurchaseId_then400() throws Exception {
    mockMvc.perform(get("/invalid").with(jwt())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetPurchases_whenAll_thenReturnAll() throws Exception {
    given(purchaseService.getPurchases(null, "asc.datetime", 50, 0))
        .willReturn(PurchaseMsResponseUtil.defaultPurchaseListResponse());
    mockMvc
        .perform(get("/").with(jwt()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "[{\"id\":\"PUR0000001\",\"order_id\":\"ORD0000001\",\"supplier\":{\"id\":\"SUP0000001\",\"name\":\"Supplier A\"},"
                        + "\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\",\"name\":\"Heineken\",\"price\":10,\"category\":\"beer\"}"
                        + "}],\"status\":\"processing\",\"datetime\":\"07-29-2022T12:16:00.000Z\"}]"));
  }

  @Test
  public void testGetPurchases_whenOrderId_then200() throws Exception {
    given(purchaseService.getPurchases("ORD0000001", "asc.datetime", 50, 0))
        .willReturn(PurchaseMsResponseUtil.defaultPurchaseListResponse());
    mockMvc
        .perform(get("/?order-id=ORD0000001").with(jwt()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "[{\"id\":\"PUR0000001\",\"order_id\":\"ORD0000001\",\"supplier\":{\"id\":\"SUP0000001\",\"name\":\"Supplier A\"},"
                        + "\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\",\"name\":\"Heineken\",\"price\":10,\"category\":\"beer\"}"
                        + "}],\"status\":\"processing\",\"datetime\":\"07-29-2022T12:16:00.000Z\"}]"));
  }

  @Test
  public void testGetPurchases_whenInvalid_then400() throws Exception {
    given(purchaseService.getPurchases("invalid", "asc.datetime", 50, 0))
        .willReturn(PurchaseMsResponseUtil.defaultPurchaseListResponse());
    mockMvc
        .perform(get("/?order-id=invalid").with(jwt()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "{\"error_code\":\"E_PUR_400\",\"description\":\"Missing required request parameters\"}"));
  }
}
