/**
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.Similarity;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.metrics.StringMetrics;

import com.aliasi.util.Distance;

import br.usp.icmc.gazetteer.TAD.Place;
import info.debatty.java.stringsimilarity.NGram;

public class Metrics implements Distance<Place> {

	private String method;
	
	public Metrics(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public double distance(Place pl1, Place pl2) {
		
		String wordA = pl1.getLocation();
		String wordB = pl2.getLocation();
		
		switch(method){
		case "Levenshtein":
			StringMetric lev = StringMetrics.levenshtein();
			return 1 - lev.compare(wordA, wordB);
		case "SmithWaterman":
			StringMetric smith = StringMetrics.smithWaterman();
			return 1 - smith.compare(wordA, wordB); 
		case "Jaccard":
			StringMetric jac = StringMetrics.jaccard();
			return 1 - jac.compare(wordA,wordB);
		case "Cosine":
			StringMetric cos = StringMetrics.cosineSimilarity();	
			return 1 - cos.compare(wordA, wordB); 
		case "BlockDistance":
			StringMetric bloc = StringMetrics.blockDistance();
			return 1 - bloc.compare(wordA, wordB);
		case "EuclideanDistance":
			StringMetric eucl = StringMetrics.euclideanDistance();	
			return 1 - eucl.compare(wordA, wordB);
		case "LongestCommonSubstring":
			StringMetric log = StringMetrics.longestCommonSubstring();
			return 1- log.compare(wordA, wordB);
		case "JaroWinkler" :
			StringMetric jaro = StringMetrics.jaroWinkler();
			return 1 - jaro.compare(wordA, wordB);
		case "Bigram":
			NGram bigram = new NGram(2);
			return bigram.distance(wordA, wordB);
		default:
			Levenshtein s = new Levenshtein();
			return 1 - s.compare(wordA, wordB); 
		}
	}

	public double getSimilarity(String wordA, String wordB) {
		
		
		switch(method){
		case "Levenshtein":
			StringMetric lev = StringMetrics.levenshtein();
			return lev.compare(wordA, wordB);
		case "SmithWaterman":
			StringMetric smith = StringMetrics.smithWaterman();
			return smith.compare(wordA, wordB); 
		case "Jaccard":
			StringMetric jac = StringMetrics.jaccard();
			return jac.compare(wordA,wordB);
		case "Cosine":
			StringMetric cos = StringMetrics.cosineSimilarity();	
			return cos.compare(wordA, wordB); 
		case "BlockDistance":
			StringMetric bloc = StringMetrics.blockDistance();
			return bloc.compare(wordA, wordB);
		case "EuclideanDistance":
			StringMetric eucl = StringMetrics.euclideanDistance();	
			return eucl.compare(wordA, wordB);
		case "LongestCommonSubstring":
			StringMetric log = StringMetrics.longestCommonSubstring();
			return log.compare(wordA, wordB);
		case "JaroWinkler" :
			StringMetric jaro = StringMetrics.jaroWinkler();
			return jaro.compare(wordA, wordB);
		case "Bigram":
			NGram bigram = new NGram(2);
			return 1.0f - bigram.distance(wordA, wordB);
		default:
			Levenshtein s = new Levenshtein();
			return s.compare(wordA, wordB); 
		}
	}


}
