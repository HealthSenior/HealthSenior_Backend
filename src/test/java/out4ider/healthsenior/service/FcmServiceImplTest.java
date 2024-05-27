//package out4ider.healthsenior.service;
//
//import org.junit.jupiter.api.Test;
//import out4ider.healthsenior.dto.FcmSendDto;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FcmServiceImplTest {
//    FcmServiceImpl fcmService = new FcmServiceImpl();
//
//    @Test
//    public void credentialTest() throws IOException {
//        String accessToken;
//        try {
//            accessToken = fcmService.getAccessToken();
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
//        System.out.println("..");
//    }
//
//
//    @Test
//    void sendMessageTo() {
//        try {
//            fcmService.sendMessageTo(FcmSendDto.builder()
//                    .title("종원아")
//                    .body("이거 보이면 말해줘").build(),"cEo-4VEBStWi7BJTvo5IkK:APA91bGNsqOG80kh9z7yDQTORB0kYzRnHUTdeWCGfEGqsVVbv_GAYe-IaEQk9Dx9yP_Xd7K85Rq5N-eE4eujiHkwBwBgb0hSSh13vgQRP46i0njXSD5zx8D2yhha5OstNx4x6REIdDLU");
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }
//}