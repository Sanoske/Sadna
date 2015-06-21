package part.three;

import mulan.classifier.MultiLabelOutput;
import mulan.classifier.lazy.MLkNN;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.meta.thresholding.MLPTO;
import mulan.classifier.neural.BPMLL;
import mulan.classifier.neural.MMPLearner;
import mulan.classifier.transformation.AdaBoostMH;
import mulan.classifier.transformation.PPT;
import mulan.data.MultiLabelInstances;
import weka.core.Instance;

public class CV_PPT extends CVMulan {
	
	private PPT learner;
	
	public CV_PPT(PPT l){
		learner = l;
	}
	@Override
	public void train(MultiLabelInstances m) throws Exception {
		learner.build(m);
	}

	@Override
	public MultiLabelOutput predict(Instance Ins) throws Exception {
		return learner.makePrediction(Ins);
	}

}
