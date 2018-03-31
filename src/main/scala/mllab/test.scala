import org.rogach.scallop._




object HelloWorld {
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val hyper = opt[String](
      default = Some("animal=cat, color=black"),
      descr = "hyperparameters to pass to the algorithm"
    )
    verify()
  }



  def main(args: Array[String]): Unit = {

    val conf = new Conf(args)
    if (!conf.hyper().isEmpty())
      println("Chosen hyperparameters: " + conf.hyper)

  }
}
