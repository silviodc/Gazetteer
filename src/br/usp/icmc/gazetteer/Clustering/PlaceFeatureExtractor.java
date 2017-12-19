package br.usp.icmc.gazetteer.Clustering;
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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;

import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Counter;
import com.aliasi.util.FeatureExtractor;
import com.aliasi.util.ObjectToCounterMap;
import com.aliasi.util.Strings;

import br.usp.icmc.gazetteer.TAD.Place;

public class PlaceFeatureExtractor<E> implements FeatureExtractor<Place>,
Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final TokenizerFactory mTokenizerFactory;

	/**
	 * Construct a Place-based feature extractor from the
	 * specified tokenizer factory.
	 *
	 * @param factory Tokenizer factory to use for tokenization.
	 */
	public PlaceFeatureExtractor(TokenizerFactory factory) {
		mTokenizerFactory = factory;
	}

	/**
	 * Return the feature vector for the specified character sequence.
	 * The keys are the tokens extracted and their values is the count
	 * of the token in the input character sequence.
	 *
	 * @param in Character sequence from which to extract features.
	 * @return Mapping from tokens in the input sequence to their
	 * counts.
	 */
	public Map<String,Counter> features(Place in) {
		ObjectToCounterMap<String> map = new ObjectToCounterMap<String>();
		char[] cs = Strings.toCharArray(in.getLocation());
		Tokenizer tokenizer = mTokenizerFactory.tokenizer(cs,0,cs.length);
		for (String token : tokenizer)
			map.increment(token);
		return map;
	}

	/**
	 * Returns a description of this token feature extractor including
	 * its contained tokenizer factory.  This method calls the {@code
	 * toString()} method of the contained tokenizer factory.
	 *
	 * @return A description of this token feature
	 * extractor and its contained tokenizer factory.
	 */
	@Override public String toString() {
		return "com.aliasi.tokenizer.TokenFeatureExtractor("
				+ mTokenizerFactory
				+ ")";
	}

	private Object writeReplace() {
		return new Externalizer(this);
	}

	static class Externalizer extends AbstractExternalizable {
		static final long serialVersionUID = 4716086241839692672L;
		private final PlaceFeatureExtractor mExtractor;
		public Externalizer() {
			this(null);
		}
		public Externalizer(PlaceFeatureExtractor extractor) {
			mExtractor = extractor;
		}
		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			AbstractExternalizable.compileOrSerialize(mExtractor.mTokenizerFactory,
					out);
		}
		@Override
		public Object read(ObjectInput in)
				throws ClassNotFoundException, IOException {

			TokenizerFactory factory
			= (TokenizerFactory) in.readObject();
			return new TokenFeatureExtractor(factory);
		}
	}
}

