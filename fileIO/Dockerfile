FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN apt-get update

WORKDIR /fileIO
ADD . /fileIO
CMD sbt run