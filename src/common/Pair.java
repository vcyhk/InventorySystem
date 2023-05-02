package common;

public class Pair<S, T>
{
    private S first;
    private T second;
    
    public Pair(S n1, T n2)
    {
        first = n1;
        second = n2;
    }
    
    public S getFirst()
    {
        return first;
    }
    
    public T getSecond()
    {
        return second;
    }    
    
    public void setFirst(S v)
    {
        first = v;
    }
    
    public void setSecond(T v)
    {
        second = v;
    }
    
    @Override
    public String toString()
    {
        return first + "\t" + second;
    }
}
