package nonregressiontest.component.collectiveitf.multicast.classbased;

import java.util.List;

import org.objectweb.proactive.core.component.type.annotations.collective.ClassDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatchMode;

import nonregressiontest.component.collectiveitf.multicast.WrappedInteger;

// only testing one mode
@ClassDispatchMetadata(mode=@ParamDispatchMetadata(mode=ParamDispatchMode.ONE_TO_ONE))
public interface OneToOneMulticast {
    
    public List<WrappedInteger> dispatch(List<WrappedInteger> l);

}
