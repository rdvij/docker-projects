FROM postgres:14.9
USER postgres
RUN whoami
ADD ./scripts/init-config-service.sql /docker-entrypoint-initdb.d/
ENTRYPOINT ["docker-entrypoint.sh"]
EXPOSE 5432
CMD ["postgres"]