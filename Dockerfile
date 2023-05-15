FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1
RUN sbt update
COPY . /romme 
WORKDIR /romme
CMD sbt run