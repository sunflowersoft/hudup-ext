package net.hudup.core.alg;

import java.io.Serializable;

import net.hudup.core.alg.AlgDesc.MethodologyType;

/**
 * This interface declares the most powerful algorithm and such algorithm is atomic.
 * For example, some algorithms require recommendation results from recommendation algorithms.
 * However, this interface is not categorized in {@link MethodologyType}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Aug extends RemoteAlg, ExecutableAlg, ModelBasedAlg, SupportCacheAlg, NoteAlg, Serializable {

	
}
