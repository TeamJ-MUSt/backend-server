package TeamJ.MUSt;

import TeamJ.MUSt.exception.NoSearchResultException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


public class PythonExecutorTest {

    @Test
    @DisplayName("검색 결과에 오류가 있으면 예외 발생")
    public void quoteTest() throws Exception{
        assertThrows(NoSearchResultException.class, ()->{
            PythonExecutor.callBugsApi("a milli", "lil wayne");
        });
    }
}
