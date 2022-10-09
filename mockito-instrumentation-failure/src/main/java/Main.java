import org.mockito.Mockito;

class C {
  void toHook(String input) {
    System.out.println("C's implementation say: " + input);
  }
}

public class Main {

  public static void successful_instrumentation() {
    System.out.println("==========");
    System.out.println("Successful case:");
    C spiedC = Mockito.spy(C.class);
    Mockito.doAnswer(invocation -> {
      Object[] arguments = invocation.getArguments();
      System.out.println("Spied implementation say: " + arguments[0]);
      return null;
    }).when(spiedC).toHook(Mockito.any());

    spiedC.toHook("Hiahia");
  }

  public static void failed_instrumentation() {
    System.out.println("==========");
    System.out.println("Failed case:");
    C spiedC = Mockito.spy(C.class);
    C tmpC = Mockito.doAnswer(invocation -> {
      Object[] arguments = invocation.getArguments();
      System.out.println("Spied implementation say: " + arguments[0]);
      return null;
    }).when(spiedC);
    // This is what might happen in an IDE like IntelliJ !!!
    tmpC.toString();
    tmpC.toHook(Mockito.any());

    spiedC.toHook("Hiahia");
  }

  public static void main(String[] args){
    successful_instrumentation();
    failed_instrumentation();
  }
}
