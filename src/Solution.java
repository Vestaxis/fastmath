public class Solution
{
  public int solution(int[] A)
  {
    int depth = 0;

    int P = 0, Q = -1, R = -1;

    for (int i = 1; i < A.length; i++)
    {
      if (Q < 0 && A[i] >= A[i - 1]) Q = i - 1;

      if ((Q >= 0 && R < 0) && (A[i] <= A[i - 1] || i + 1 == A.length))
      {
        if (A[i] <= A[i - 1]) R = i - 1;
        else R = i;
        depth = Math.max(depth, Math.min(A[P] - A[Q], A[R] - A[Q]));
        P = i - 1;
        Q = R = -1;
      }
    }
    if (depth == 0) depth = -1;

    return depth;
  }

}
