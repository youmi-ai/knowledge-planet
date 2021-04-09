1. 设计HashMap

   

   ```java
   Node[] table = new Node[]
   class Node {
     K k;
     V v;
     int hash; // 根据key能够得到数组下标
   }
   ```

2. 根据Key 得到数组下标

下标是int：尽量分散；首次得到int值尽量在0-15之间

```java
int hash = key.hashCode()%16 = 0----15
```

3. 效率

```java
int hash = key.hashCode();

key.hashCode()&(16-1) ---- key.hashCode()&(n-1)
```

4. 充分利用hashCode

key.hashCode高16^ 低16&（n-1）