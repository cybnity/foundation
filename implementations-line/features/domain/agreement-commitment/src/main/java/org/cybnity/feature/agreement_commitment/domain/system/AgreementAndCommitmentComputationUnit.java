package org.cybnity.feature.agreement_commitment.domain.system;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Start a composition of gateway Verticle supporting the application security
 * services provided by the processing units of the domain.
 */
public class AgreementAndCommitmentComputationUnit extends AbstractVerticle {

	/**
	 * Default start method regarding the server.
	 *
	 * @param args None pre-required.
	 */
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		//vertx.deployVerticle(new AssetControlComputationUnit());
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		startPromise.complete();
		/*vertx.deployVerticle(
				Set each feature unit regarding this domain api
				CreateAssetFeature.class.getName(), event -> {
					if (event.succeeded()) {
						System.out.println(
								CreateAssetFeature.class.getSimpleName() + " successly deployed: " + event.result());
						startPromise.complete();
					} else {
						System.out.println(CreateAssetFeature.class.getSimpleName() + " deployment failed: ");
						event.cause().printStackTrace();
						startPromise.fail(event.cause());
					}
				});*/
	}
}
