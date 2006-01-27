package nonregressiontest.component.collectiveitf.multicast;

import org.objectweb.proactive.core.component.type.annotations.collective.ClassDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatchMode;

@ClassDispatchMetadata(mode=@ParamDispatchMetadata(mode=ParamDispatchMode.ONE_TO_ONE))
public interface OneToOneItfAnnotatedServerItf extends MulticastTestItf {

}
