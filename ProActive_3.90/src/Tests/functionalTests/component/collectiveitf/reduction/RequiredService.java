package functionalTests.component.collectiveitf.reduction;

import java.util.List;

import org.objectweb.proactive.core.component.type.annotations.multicast.MethodDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;
import org.objectweb.proactive.core.component.type.annotations.multicast.Reduce;
import org.objectweb.proactive.core.component.type.annotations.multicast.ReduceMode;
import org.objectweb.proactive.core.group.Dispatch;
import org.objectweb.proactive.core.group.DispatchMode;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;


public interface RequiredService {

    @Reduce(reductionMode = ReduceMode.CUSTOM, customReductionMode = SumReduction.class)
    @MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.ONE_TO_ONE))
    @Dispatch(mode = DispatchMode.STATIC_ROUND_ROBIN)
    public IntWrapper method1(List<Integer> parameters);

}
