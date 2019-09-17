/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.logistic.NextUpdate;


/**
 * Traditional recommendation study focuses on inherent information about users and items and how to recommend such relevant items to such users.
 * Database used to build up recommendation algorithms is in form of rating matrix composed of ratings that users give to items.
 * However, currently, additional contextual factors such as time, place, condition and situation existing in real world are considered in recommendation study.
 * Context is modeled by {@link net.hudup.core.data.ctx.Context} interface.
 * There are three approaches (Ricci et al., Recommender Systems Handbook, pp. 232-233) to apply context into recommendation process:
 * <ul>
 * <li>
 * <i>Contextual pre-filtering</i>: Firstly, given context {@code c} is used to select user-item pairs (u, i) which are more relevant to this context, leading to obtain the aware-context cross domain U x I.
 * After that traditional recommendation algorithm is taken on such cross domain.
 * </li>
 * <li>
 * <i>Contextual post-filtering</i>: Firstly, traditional recommendation algorithm is used to produce the list of recommended item.
 * After that context {@code c} is used to fine-tune this list in order to remove irrelevant items according to concrete context.
 * </li>
 * <li>
 * <i>Contextual modeling</i>: The modern context-aware recommendation algorithm is used directly on context-aware cross domain U x I x C including three sets such as users, items and context sets.
 * </li>
 * </ul>
 * This interface models the approach of contextual pre-filtering in which contexts are used to pre-filter dataset on which recommendation process are taken to produce context-aware recommended list. 
 * This class inherits directly {@link CompositeRecommender}. Note that {@link CompositeRecommender} represents the recommendation strategy, called {@code composite recommender}.
 * {@link CompositeRecommender} is combination of other {@link Recommender} algorithms in order to produce the best list of recommended items.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public interface RecommendContextPrefilter extends CompositeRecommender {

	
	/**
	 * Setting up the original dataset and the filtered dataset based on the specified dataset and context list.
	 * Actually, this method uses the specified list of contexts to filter the original dataset so as to achieve the filtered dataset which belongings to the context list.
	 * 
	 * @param dataset specified dataset.
	 * @param contextList specified context list.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Dataset dataset, ContextList contextList) throws RemoteException;


}
