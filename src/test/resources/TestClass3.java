package samples;

import lombok.AccessLevel;
import lombok.Setter;
import java.util.Date;
import java

class TestClass3 {
	@Setter String property1;
	@Setter int property2;
	@Setter(AccessLevel.PRIVATE) String property3;
	@Setter(AccessLevel.PUBLIC) Date property4;
}