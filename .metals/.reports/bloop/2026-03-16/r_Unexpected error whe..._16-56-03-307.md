error id: CIO2qP3lr5CqHa/pW6/aHg==
### Bloop error:

Unexpected error when compiling principle: java.io.FileNotFoundException: <WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/structure/PackageStructureHints1Finder.scala (No such file or directory)
	at java.base/java.io.RandomAccessFile.open0(Native Method)
	at java.base/java.io.RandomAccessFile.open(RandomAccessFile.java:356)
	at java.base/java.io.RandomAccessFile.<init>(RandomAccessFile.java:273)
	at java.base/java.io.RandomAccessFile.<init>(RandomAccessFile.java:223)
	at bloop.io.ByteHasher$.hashFile(ByteHasher.scala:25)
	at bloop.io.ByteHasher$.hashFileContents(ByteHasher.scala:20)
	at bloop.io.SourceHasher$.$anonfun$findAndHashSourcesInProject$16(SourceHasher.scala:152)
	at monix.eval.internal.TaskRunLoop$.startLight(TaskRunLoop.scala:271)
	at monix.eval.Task.runAsyncOptF(Task.scala:812)
	at monix.eval.Task.runAsyncOpt(Task.scala:710)
	at monix.eval.Task.runAsync(Task.scala:660)
	at monix.reactive.internal.operators.MapParallelUnorderedObservable$MapAsyncParallelSubscription.process(MapParallelUnorderedObservable.scala:122)
	at monix.reactive.internal.operators.MapParallelUnorderedObservable$MapAsyncParallelSubscription.$anonfun$onNext$2(MapParallelUnorderedObservable.scala:151)
	at monix.execution.Ack$$anon$1.run(Ack.scala:54)
	at monix.execution.internal.InterceptRunnable.run(InterceptRunnable.scala:27)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	at java.base/java.lang.Thread.run(Thread.java:1583)
#### Short summary: 

Unexpected error when compiling principle: java.io.FileNotFoundException: <WORKSPACE>/src/main/scala...