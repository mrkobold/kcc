
int sum(int a, int b)
{
    return a + b;
}

int complicated(int a, int b, int c)
{
    return a + b * (c - 4);
}

int main()
{
    int a;
    a = 1;
    int b;
    b = 2;
    int c;
    c = 6;
    int d;
    d = complicated(a,b,c);
}