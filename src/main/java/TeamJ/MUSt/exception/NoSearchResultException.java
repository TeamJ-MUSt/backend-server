package TeamJ.MUSt.exception;

public class NoSearchResultException extends Exception{
    public NoSearchResultException() {
        super("검색 결과가 없습니다.");
    }
}
