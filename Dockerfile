FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1
RUN sbt compile
COPY . /romme 
WORKDIR /romme
CMD sbt run