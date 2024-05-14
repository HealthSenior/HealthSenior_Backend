//package out4ider.healthsenior.service;
////
////import org.junit.jupiter.api.Test;
////import out4ider.healthsenior.dto.FcmSendDto;
////
////import java.io.IOException;
////
////import static org.junit.jupiter.api.Assertions.*;
////
////class FcmServiceImplTest {
////    FcmServiceImpl fcmService = new FcmServiceImpl();
////
////    @Test
////    public void credentialTest() throws IOException {
////        String accessToken;
////        try {
////            accessToken = fcmService.getAccessToken();
////        }
////        catch (Exception e){
////            System.out.println(e);
////        }
////        System.out.println("..");
////    }
////
////
////    @Test
////    void sendMessageTo() {
////        try {
////            fcmService.sendMessageTo(FcmSendDto.builder()
////                    .title("title")
////                    .body("message").build(),"");
////        } catch (IOException e) {
////            System.out.println(e);
////        }
////    }
////}