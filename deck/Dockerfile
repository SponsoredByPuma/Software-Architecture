FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN apt-get update

WORKDIR /deck
ADD . /deck
CMD sbt run