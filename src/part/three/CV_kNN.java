package part.three;

import weka.core.Instance;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.lazy.BRkNN;
import mulan.classifier.lazy.MLkNN;
import mulan.data.MultiLabelInstances;

public class CV_kNN extends CVMulan{
	
	private BRkNN learner;
	
	public CV_kNN(BRkNN l){
		learner = l;
	}
	
	@Override
	public void train(MultiLabelInstances trainIns) throws Exception {
		learner.build(trainIns);
	}

	@Override
	public MultiLabelOutput predict(Instance ins) throws Exception{
		return learner.makePrediction(ins);
	}
	
}
