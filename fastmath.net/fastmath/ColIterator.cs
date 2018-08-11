using Sharpen;

namespace fastmath
{
	public class ColIterator<V> : System.Collections.Generic.IEnumerator<V>, System.Collections.Generic.IEnumerable
		<V>
		where V : fastmath.Vector
	{
		internal readonly fastmath.AbstractMatrix x;

		internal int i;

		public ColIterator(fastmath.AbstractMatrix x)
		{
			this.x = x;
			i = 0;
		}

		public virtual bool MoveNext()
		{
			return i < x.getColCount();
		}

		public virtual V Current
		{
			get
			{
				i++;
				return (V)x.col(i - 1);
			}
		}

		public virtual void remove()
		{
			throw new System.NotSupportedException();
		}

		public virtual System.Collections.Generic.IEnumerator<V> GetEnumerator()
		{
			return this;
		}

		public virtual java.util.stream.Stream<V> stream(bool parallel)
		{
			return java.util.stream.StreamSupport.stream(spliterator(), parallel);
		}
	}
}
